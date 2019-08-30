package com.nesterov.owngame;

import android.animation.Animator;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    int CLIENTS_COUNT;
    DataOutputStream[] outputStreams;

    ImageButton btnPlus, btnMinus;
    TextView connectionStatus;
    EditText finalAnswer,finalBet;
    Button sendBet, sendFinalAnswer;
    Button zeroBtn;
    ListView scoreList;

    ImageView zhdanov;

    LinearLayout buttons;
    ProgressBar pb;


    private Handler handler = new Handler();
    private Socket socket;
    private BufferedReader inputStream;

    private boolean isServer = false;
    String name;

    ArrayList<ScoreItem> items = new ArrayList<>();
    com.nesterov.owngame.ListAdapter adapter;

    int ItsZhdanovTime = 0;

    private boolean searchNetwork() {
        log("Поиск сервера...");
        String range = "192.168.1.";
        for (int i = 1; i <= 255; i++) {
            String ip = range + i;
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(ip, 9000), 50);
                outputStreams = new DataOutputStream[1];
                outputStreams[0] = new DataOutputStream(socket.getOutputStream());
                inputStream = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                outputStreams[0].write(("name:"+name+"\n").getBytes());
                Log.e("OWNGAME_SEND","name:"+name+"\n");
                log("Подключено. Ожидание других игроков...");
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;

    }

    private void runNewChatServer() {
        ServerSocket serverSocket;
        outputStreams = new DataOutputStream[CLIENTS_COUNT];
        try {
            serverSocket = new ServerSocket(9000);
            for (int i = 0; i < CLIENTS_COUNT; i++) {
                log("Ожидание подключения игроков...");
                socket = serverSocket.accept();
                log((i+2)+ "/" + (CLIENTS_COUNT+1) + " игроков подключено");
                outputStreams[i] = new DataOutputStream(socket.getOutputStream());
                new ClientThread(socket).start();
            }
            items.add(new ScoreItem(0,name,true));
            log("Начало игры...");
            for (int i = 0; i < outputStreams.length; i++) {
                for (int j = 0; j < items.size(); j++) {
                    String Message = "name:" + items.get(j).getName() + "\n";
                    outputStreams[i].write(Message.getBytes());
                    Log.e("OWNGAME_SEND",Message);
                    Thread.sleep(500);
                }
                outputStreams[i].write(("that is all\n").getBytes());
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new com.nesterov.owngame.ListAdapter(MainActivity.this,R.layout.item,items);
                    scoreList.setAdapter(adapter);
                    buttons.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    connectionStatus.setVisibility(View.GONE);
                }
            });

        } catch (IOException e) {

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        connectionStatus = findViewById(R.id.connectionStatus);
        btnPlus = findViewById(R.id.plusBtn);
        btnMinus = findViewById(R.id.minusBtn);
        scoreList = findViewById(R.id.scoreList);
        buttons = findViewById(R.id.buttons);
        pb = findViewById(R.id.ProgressBar);
        zhdanov = findViewById(R.id.zhdanov);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Intent myIntent = getIntent();
                isServer = myIntent.getBooleanExtra("isServer",false);
                int playersNum = myIntent.getIntExtra("playersNum",0);
                name = myIntent.getStringExtra("name");
                try {
                    if (isServer) {
                        CLIENTS_COUNT = playersNum-1;
                        runNewChatServer();
                        isServer = true;
                    } else {
                        while (!searchNetwork()) {}
                        while (true) {
                            String mmessage = inputStream.readLine();
                            if (mmessage != null) {
                                Log.e("OWNGAME",mmessage);
                                log2(mmessage);
                            }
                        }
                    }
                } catch (IOException e) {
                    log("Чё-то всё сломалось:(");
                    e.printStackTrace();
                }
            }
        });
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outputStreams == null) {
                    return;
                }
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < outputStreams.length; i++) {
                                String Message = "plus:"+name+"\n";
                                outputStreams[i].write(Message.getBytes());
                                Log.e("OWNGAME_SEND",Message);
                            }
                            if (isServer)
                                log2("plus:"+name);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (outputStreams == null) {
                    return;
                }
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < outputStreams.length; i++) {
                                String Message = "minus:"+name+"\n";
                                outputStreams[i].write(Message.getBytes());
                                Log.e("OWNGAME_SEND",Message);
                            }
                            if (isServer)
                                log2("minus:"+name);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
            }
        });
        thread.start();

    }

    private void log(final String message) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                connectionStatus.setText(connectionStatus.getText() + "\n" + message);
            }
        });
    }
    private void log2(final String message) {
        handler.post(new Runnable() {

            @Override
            public void run() {
                if (message.contains("plus:")) {
                    String nm = message.replace("plus:","");
                    for (ScoreItem i: items) {
                        if (i.getName().equals(nm))
                            i.setScore(i.getScore()+1);
                    }
                    Collections.sort(items, new Comparator<ScoreItem>() {
                        @Override
                        public int compare(ScoreItem o1, ScoreItem o2) {
                            if (o1.getScore() < o2.getScore())
                                return 1;
                            else if (o1.getScore() > o2.getScore())
                                return -1;
                            else
                                return 0;
                        }
                    });
                    adapter.notifyDataSetChanged();
                } else if (message.contains("minus:")) {
                    String nm = message.replace("minus:","");
                    for (ScoreItem i: items) {
                        if (i.getName().equals(nm))
                            i.setScore(i.getScore()-1);
                    }
                    Collections.sort(items, new Comparator<ScoreItem>() {
                        @Override
                        public int compare(ScoreItem o1, ScoreItem o2) {
                            if (o1.getScore() < o2.getScore())
                                return 1;
                            else if (o1.getScore() > o2.getScore())
                                return -1;
                            else
                                return 0;
                        }
                    });
                    adapter.notifyDataSetChanged();
                    ItsZhdanovTime++;
                    if (ItsZhdanovTime == 5) {
                        zhdanov.animate().translationY(zhdanov.getHeight()).setDuration(0).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                zhdanov.setVisibility(View.VISIBLE);
                                zhdanov.animate().setDuration(1000).translationY(0).setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        zhdanov.animate().translationY(zhdanov.getHeight()).setDuration(1000).setListener(new Animator.AnimatorListener() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {


                                            }

                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                zhdanov.setVisibility(View.GONE);
                                            }

                                            @Override
                                            public void onAnimationCancel(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animator animation) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                });
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        ItsZhdanovTime = 0;
                    }
                } else if (message.contains("name:")) {
                    items.add(new ScoreItem(0,message.replace("name:",""), message.replace("name:", "").equals(name)));
                } else if (message.equals("that is all")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new com.nesterov.owngame.ListAdapter(MainActivity.this,R.layout.item,items);
                            scoreList.setAdapter(adapter);
                            buttons.setVisibility(View.VISIBLE);
                            pb.setVisibility(View.GONE);
                            connectionStatus.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public class ClientThread extends Thread {
        protected Socket mSocket;

        public ClientThread(Socket clientSocket) {
            this.mSocket = clientSocket;
        }

        public void run() {
            try {
                BufferedReader mInputStream = new BufferedReader(new InputStreamReader(
                        mSocket.getInputStream()));
                while (true) {
                    String mmessage = mInputStream.readLine();
                    if (mmessage != null) {
                        Log.e("OWNGAME",mmessage);
                        if (mmessage.contains("plus:") || mmessage.contains("minus:")) {
                            for (int i = 0; i < outputStreams.length; i++) {
                                String Message = mmessage+"\n";
                                outputStreams[i].write(Message.getBytes());
                                Log.e("OWNGAME_SEND",Message);
                            }
                            log2(mmessage);
                        } else if (mmessage.contains("name:")){
                            items.add(new ScoreItem(0,mmessage.replace("name:",""), mmessage.replace("name:", "").equals(name)));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}