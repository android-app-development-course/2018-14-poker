package com.example.administrator.puke;

import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.transition.TransitionManager;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;



public class GameActivity extends AppCompatActivity {

    //Puke图片
    private int pukeId[]={
            R.drawable.p1,R.drawable.p2,R.drawable.p3,R.drawable.p4,
            R.drawable.p5,R.drawable.p6,R.drawable.p7, R.drawable.p8,
            R.drawable.p9,R.drawable.p10,R.drawable.p11,R.drawable.p12,
            R.drawable.p13,R.drawable.p14, R.drawable.p15,R.drawable.p16,
            R.drawable.p17,R.drawable.p18,R.drawable.p19,R.drawable.p20,
            R.drawable.p21,R.drawable.p22,R.drawable.p23,R.drawable.p24,
            R.drawable.p25,R.drawable.p26,R.drawable.p27,R.drawable.p28,
            R.drawable.p29,R.drawable.p30,R.drawable.p31,R.drawable.p32,
            R.drawable.p33,R.drawable.p34,R.drawable.p35,R.drawable.p36,
            R.drawable.p37,R.drawable.p38, R.drawable.p39,R.drawable.p40,
            R.drawable.p41,R.drawable.p42,R.drawable.p43,R.drawable.p44,
            R.drawable.p45,R.drawable.p46,R.drawable.p47,R.drawable.p48,
            R.drawable.p49,R.drawable.p50,R.drawable.p51,R.drawable.p52
    };
    //界面布局
    private ConstraintLayout constraintLayout;
    private ConstraintSet applyConstraintSet = new ConstraintSet();
    //扑克选中标志
    private boolean pukeSelect[]=new boolean[13];
    //扑克图片对应id
    private int imageId[]={R.id.imageview1,R.id.imageview2,R.id.imageview3,R.id.imageview4,
            R.id.imageview5,R.id.imageview6,R.id.imageview7,R.id.imageview8,R.id.imageview9,
            R.id.imageview10,R.id.imageview11,R.id.imageview12,R.id.imageview13};
    //对手扑克图片对应id
    private int imagePlayId[]={R.id.showPuke1,R.id.showPuke2,R.id.showPuke3,R.id.showPuke4,R.id.showPuke5};
    //扑克已出标志
    private boolean hasPlay[]={false,false,false,false,false,false,
            false,false,false,false,false,false,false};
    //扑克高宽设置
    private ViewGroup.LayoutParams params=new ViewGroup.LayoutParams(180,250);
    //记录手牌数
    private int hasPukeNum;
    //扑克数
    private int pukeCount=13;
    //服务器发牌记录
    private int shufflePuke[]=new int[13];
    //服务器发牌标志
    private boolean isshufflePuke=false;
    //保存自己出牌
    int num[]=new int[5];
    private int numCount;
    //保存对手的出牌
    private int showPlayPuke[]=new int[5];
    private int playPukeCount;
    //用于懒得优化的代码
    private String str;
    //socket相关流
    private Socket socket;
    private InputStream is;
    private OutputStream os;
    private InputStreamReader reader;
    private BufferedReader bufReader;
    //用于线程更新画面
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        EditText neterror=(EditText)findViewById(R.id.netError);
        neterror.setVisibility(View.INVISIBLE);
        handler=new Handler();
        hidOrderBtn(View.INVISIBLE);
        hidPlayImage(View.INVISIBLE,5);
        hasPukeNum=13;
        for(int i=0;i<13;i++){
            hasPlay[i]=false;
        }
        new Thread(){
             public void run(){
                 super.run();
                try{
                    socket = new Socket("192.168.123.106", 12306);//192.168.123.106本机的IP地址，端口号为12306
                    //拿到socket的输入流，这里存储的是服务器返回的数据
                    is = socket.getInputStream();
                    //解析服务器返回的数据
                    while(true){
                        reader = new InputStreamReader(is);
                        bufReader = new BufferedReader(reader);
                        String s = null;
                        while((s = bufReader.readLine()) != null) {
                            if(s.startsWith("shufflePuke ")){
                                s = s.replaceFirst("shufflePuke ","");
                                String[] strarray=s.split(" ");
                                for(int i=0;i<13;i++){
                                    shufflePuke[i]=Integer.valueOf(strarray[i]);
                                }
                                isshufflePuke=true;
                            }
                            else if(s.startsWith("play pass")){
                                handler.post(passRunnable);
                            }
                            else if(s.startsWith("play ")){
                                s=s.replaceFirst("play ","");
                                String[] strarray=s.split(" ");
                                int i;
                                ImageView iv;
                                for(i=0;i<strarray.length;i++){
                                    showPlayPuke[i]=Integer.valueOf(strarray[i]);
                                    iv = (ImageView) findViewById(imagePlayId[i]);
                                    iv.setImageResource(pukeId[showPlayPuke[i]]);
                                }
                                playPukeCount=i;
                                handler.post(playRunnable);
                            }
                        }
                    }
                    // 关闭IO资源
                    /*bufReader.close();
                    reader.close();
                    is.close();*/

                    // 关闭输入流
                    //socket.shutdownInput();
                }catch (UnknownHostException e) {
                    handler.post(errorRunnable);
                    for(int i=0;i<3000;i++);
                    e.printStackTrace();
                } catch (IOException e) {
                    handler.post(errorRunnable);
                    for(int i=0;i<3000;i++);
                    e.printStackTrace();
                }
            }
        }.start();

        //
       /* Display display= getWindowManager().getDefaultDisplay();
        Point point =new Point();
        display.getSize(point);
        width = point.x;
        height = point.y;
        System.out.println(width);
        System.out.println(height);*/

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = this.getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    //imageView Listener
    class PukeListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            int pukeNum=getViewNum(view);
            if(hasPlay[pukeNum])return;
            applyConstraintSet.clone(constraintLayout);
            if(pukeSelect[pukeNum]){
                applyConstraintSet.setMargin(view.getId(),ConstraintSet.BOTTOM,16);
                pukeSelect[pukeNum]=false;
            }
            else{
                applyConstraintSet.setMargin(view.getId(),ConstraintSet.BOTTOM,40);
                pukeSelect[pukeNum]=true;
            }
            applyConstraintSet.applyTo(constraintLayout);
        }
    }
    //对应按钮点击
    public void beginOnClick(View view){
        sendToServet("getPuke");
        while(!isshufflePuke);
        Button  button= (Button) findViewById(R.id.button);
        button.setVisibility(View.VISIBLE);
        button= (Button) findViewById(R.id.button2);
        button.setVisibility(View.VISIBLE);
        button= (Button) findViewById(R.id.button3);
        button.setVisibility(View.INVISIBLE);
        constraintLayout = (ConstraintLayout) findViewById(R.id.cl);
        for(int i=0;i<13;i++) {
            pukeSelect[i]=false;
            ImageView imageView = new ImageView(this);
            imageView.setId(imageId[i]);
            imageView.setImageResource(pukeId[shufflePuke[i]]);
            imageView.setLayoutParams(params);
            imageView.setOnClickListener(new PukeListener());
            constraintLayout.addView(imageView);
        }
       /* applyConstraintSet.clone(constraintLayout);
        for(int i=0;i<13;i++)
        {
            ImageView imageView=(ImageView)findViewById(imageId[i]);

            TransitionManager.beginDelayedTransition(constraintLayout);
            applyConstraintSet.connect(imageView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
            applyConstraintSet.connect(imageView.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
            applyConstraintSet.setMargin(imageView.getId(),ConstraintSet.BOTTOM,16);
            applyConstraintSet.setMargin(imageView.getId(),ConstraintSet.START,1040);
        }
        applyConstraintSet.applyTo(constraintLayout);*/
        applyConstraintSet.clone(constraintLayout);
        for(int i=0;i<13;i++)
        {
            ImageView imageView=(ImageView)findViewById(imageId[i]);
            TransitionManager.beginDelayedTransition(constraintLayout);
            applyConstraintSet.connect(imageView.getId(), ConstraintSet.START, constraintLayout.getId(), ConstraintSet.START);
            applyConstraintSet.connect(imageView.getId(), ConstraintSet.BOTTOM, constraintLayout.getId(), ConstraintSet.BOTTOM);
            applyConstraintSet.setMargin(imageView.getId(),ConstraintSet.BOTTOM,16);
            applyConstraintSet.setMargin(imageView.getId(),ConstraintSet.START,264+46*i);
        }
        applyConstraintSet.applyTo(constraintLayout);
    }
    public void playOnClick(View view){
        //保存要出的牌
        String str1="play";
        int j=0;
        for(int i=0;i<13;i++){
            if(hasPlay[i])continue;
            if(pukeSelect[i]) {
                if(j>=5){
                    j++;
                    break;
                }
                str1+=" "+shufflePuke[i];
                num[j]=i;
                j++;
            }
        }
        boolean canPlay=false;
        if(j==1)canPlay=true;
        else if(j==2){
            if(shufflePuke[num[0]]/4==shufflePuke[num[1]]/4)canPlay=true;
        }
        else if(j==3){
            if(shufflePuke[num[0]]/4==shufflePuke[num[2]]/4)canPlay=true;
        }
        else if(j==4){
            if(shufflePuke[num[0]]/4==shufflePuke[num[3]]/4)canPlay=true;
        }
        else if(j==5)canPlay=true;

        if(!canPlay) {
            applyConstraintSet.clone(constraintLayout);
            for (int k = 0; k < 13; k++) {
                if (hasPlay[k]) continue;
                applyConstraintSet.setMargin(imageId[k], ConstraintSet.BOTTOM, 16);
                pukeSelect[k] = false;
            }
            applyConstraintSet.applyTo(constraintLayout);
            return;
        }
        else{
            for(int i=0;i<13;i++){
                if(hasPlay[i]) {
                    ImageView iv = (ImageView) findViewById(imageId[i]);
                    iv.setVisibility(View.INVISIBLE);
                }
            }
        }
        numCount=j;
        //将要出的牌对应ImageView移到屏幕中并改变大小
        applyConstraintSet.clone(constraintLayout);
        for(int i=0;i<j;i++){
            pukeCount--;
            hasPlay[num[i]]=true;
            applyConstraintSet.setMargin(imageId[num[i]],ConstraintSet.START,500+24*i);
            applyConstraintSet.setMargin(imageId[num[i]],ConstraintSet.BOTTOM,392);
        }
        applyConstraintSet.applyTo(constraintLayout);
        for(int i=0;i<j;i++){
            ImageView imageView=(ImageView)findViewById(imageId[num[i]]);
            ViewGroup.LayoutParams params1=imageView.getLayoutParams();
            params1.width=104;
            params1.height=144;
            imageView.setLayoutParams(params1);
        }
        //重新更改手中牌的位置
        applyConstraintSet.clone(constraintLayout);
        for(int i=0,k=0;i<13;i++){
            if(hasPlay[i])
                continue;
            else{
                ImageView imageView=(ImageView)findViewById(imageId[i]);
                applyConstraintSet.setMargin(imageView.getId(),ConstraintSet.START,264+46*k+23*(13-pukeCount));
                k++;
            }
        }
        applyConstraintSet.applyTo(constraintLayout);
        hidOrderBtn(View.INVISIBLE);
        hidPlayImage(View.INVISIBLE,5);
        sendToServet(str1);
        if(pukeCount==0){
            sendToServet("victory");
            for(int i=0;i<13;i++){
                if(hasPlay[i]) {
                    ImageView iv = (ImageView) findViewById(imageId[i]);
                    iv.setVisibility(View.INVISIBLE);
                }
            }
            Button btn=(Button)findViewById(R.id.button3);
            btn.setVisibility(View.VISIBLE);
        }

    }
    public void passOnClick(View view){
        sendToServet("pass");
        hidOrderBtn(View.INVISIBLE);
    }

    //获取Imageview对应序号
    public int getViewNum(View view){
        int i=0;
        for(;i<13;i++){
            if(view.getId()==imageId[i])
                break;
        }
        return i;
    }
    //发送请求到服务器
    public void sendToServet(String s){
        str=s;
        new Thread(){
            @Override
            public void run() {
                super.run();
                try{
                    //2.拿到客户端的socket对象的输出流发送给服务器数据
                    os = socket.getOutputStream();
                    //写入要发送给服务器的数据
                    os.write((str+"\n".toString()).getBytes());
                    os.flush();
                }catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
    //显示/隐藏玩家出牌按钮
    public void hidOrderBtn(int i){
        Button  button= (Button) findViewById(R.id.button);
        button.setVisibility(i);
        button= (Button) findViewById(R.id.button2);
        button.setVisibility(i);
    }
    //显示/隐藏自己出牌View
    public void hidImage(int i,int j){
        ImageView iv;
        while(j>0) {
            j--;
            iv = (ImageView) findViewById(imageId[num[j]]);
            iv.setVisibility(i);
        }

    }
    //显示/隐藏对方出牌View
    public void hidPlayImage(int i,int j){
        ImageView iv;
        while(j>0) {
            j--;
            iv = (ImageView) findViewById(imagePlayId[j]);
            iv.setVisibility(i);
        }

    }
    //UI更新
    Runnable   playRunnable=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            hidImage(View.INVISIBLE,numCount);
            hidPlayImage(View.VISIBLE,playPukeCount);
            hidOrderBtn(View.VISIBLE);
        }
    };
    Runnable   passRunnable=new  Runnable(){
        @Override
        public void run() {
            //更新界面
            hidOrderBtn(View.VISIBLE);
        }
    };
    Runnable   errorRunnable=new  Runnable(){
        @Override
        public void run() {
            EditText neterror=(EditText)findViewById(R.id.netError);
            neterror.setVisibility(View.VISIBLE);
            Button btn=(Button)findViewById(R.id.button3);
            btn.setVisibility(View.INVISIBLE);
        }
    };
}
