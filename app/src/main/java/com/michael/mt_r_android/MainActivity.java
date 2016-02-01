package com.michael.mt_r_android;

import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    CustomViewCanvas customViewCanvas;
    private TextView textReceive = null;
    private EditText textSend = null;
    private Button btnConnect = null;
    private Button btnSend = null;
    private Button btnDraw = null;
    private static final String ServerIP = "172.26.213.6";//"103.44.145.243";花生壳
    private static final int ServerPort = 3247;//14400;花生壳
    private Socket socket = null;
    private String strMessage;
    private boolean isConnect = false;
    private OutputStream outStream;
    private Handler myHandler = null;
    private ReceiveThread receiveThread = null;
    private boolean isReceive = false;
    protected int[] buffer=new int[362];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customViewCanvas = (CustomViewCanvas) findViewById(R.id.customViewC);

        textReceive = (TextView)findViewById(R.id.textViewReceive);
        textSend = (EditText)findViewById(R.id.editTextSend);

        btnConnect = (Button)findViewById(R.id.buttonConnect);
        btnSend = (Button)findViewById(R.id.buttonSend);
        btnDraw = (Button)findViewById(R.id.buttonDraw);



        //连接按钮的监听器
        btnConnect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!isConnect){
                    new Thread(connectThread).start();
                }

            }
        });

        //发送按钮的监听器
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                strMessage = textSend.getText().toString();
                new Thread(sendThread).start();
            }
        });

        //DrawButton
        btnDraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                for (int i=0;i<181;i++){
                    buffer[2*i]=0;
                    buffer[2*i+1]=i*100;
                }
                buffer[0]=-4000;
                buffer[1]=0;
                buffer[100]=-100;
                buffer[101]=2000;
                customViewCanvas.drawLaserMap(buffer);
            }
        });
        myHandler =new Handler(){
            @Override
            public void handleMessage(Message msg){
                textReceive.append((msg.obj).toString());
            }
        };
    }
    //连接到服务器的接口
    Runnable connectThread = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            try {
                //初始化Scoket，连接到服务器
                socket = new Socket(ServerIP, ServerPort);
                isConnect = true;
                //启动接收线程
                isReceive = true;
                receiveThread = new ReceiveThread(socket);
                receiveThread.start();
                System.out.println("----connected success----");
            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("UnknownHostException-->" + e.toString());
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.out.println("IOException" + e.toString());
            }
        }
    };
    //发送消息的接口
    Runnable sendThread = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            byte[] sendBuffer = null;
            try {
                sendBuffer = strMessage.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            try {
                outStream = socket.getOutputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                outStream.write(sendBuffer);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    };

    //接收线程
    private class ReceiveThread extends Thread{
        private InputStream inStream = null;

        private byte[] buffer;
        private int[] recBuffer;
        private String str = null;

        ReceiveThread(Socket socket){
            try {
                inStream = socket.getInputStream();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        @Override
        public void run(){
            while(isReceive){
                buffer = new byte[1448];
                recBuffer = new int[362];
                try {
                    inStream.read(buffer);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                for(int j=0;j<362;j++){
                    recBuffer[j] = (buffer[4*j+0] & 0xff) | ((buffer[4*j+1] << 8) & 0xff00)
                            | ((buffer[4*j+2] << 24) >>> 8) | (buffer[4*j+3] << 24);
                }
                buffer[0]=0;
//                Message msg = new Message();
////                msg.obj = str;
//                myHandler.sendMessage(msg);
//                try {
//                    str = new String(buffer,"UTF-8").trim();
//
//                } catch (UnsupportedEncodingException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                Message msg = new Message();
//                msg.obj = str;
//                myHandler.sendMessage(msg);

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

        if(receiveThread != null){
            isReceive = false;
            receiveThread.interrupt();
        }
    }

}
