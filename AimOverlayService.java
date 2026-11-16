package com.shahid.carromaim;

import android.app.Service;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.view.*;
import android.widget.*;

public class AimOverlayService extends Service {
    private WindowManager wm;
    private View panel;
    private AimView aim;
    private WindowManager.LayoutParams params;

    @Override public void onCreate() {
        super.onCreate();
        wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(10, 8, 10, 8);
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(0xEE171717); bg.setCornerRadius(22); bg.setStroke(3, 0xFF32E6D8);
        root.setBackground(bg);
        TextView title = new TextView(this);
        title.setText("SHAHID AIM  •  drag panel");
        title.setTextColor(Color.WHITE); title.setTextSize(13);
        root.addView(title);
        LinearLayout buttons = new LinearLayout(this);
        addButton(buttons, "S", () -> aim.setMode(0));
        addButton(buttons, "T", () -> aim.setMode(1));
        addButton(buttons, "P", () -> aim.setMode(2));
        addButton(buttons, "×", this::stopSelf);
        root.addView(buttons);
        aim = new AimView(this);
        root.addView(aim, new LinearLayout.LayoutParams(300, 250));
        TextView hint = new TextView(this);
        hint.setText("S=striker  T=coin  P=pocket • tap canvas to set");
        hint.setTextColor(0xFFDDDDDD); hint.setTextSize(10);
        root.addView(hint);
        panel = root;
        params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        params.x = 20; params.y = 120;
        root.setOnTouchListener(new View.OnTouchListener() {
            int sx, sy; float tx, ty;
            @Override public boolean onTouch(View v, MotionEvent e) {
                if (e.getAction()==MotionEvent.ACTION_DOWN) { sx=params.x; sy=params.y; tx=e.getRawX(); ty=e.getRawY(); return true; }
                if (e.getAction()==MotionEvent.ACTION_MOVE) {
                    params.x=sx+(int)(e.getRawX()-tx); params.y=sy+(int)(e.getRawY()-ty);
                    wm.updateViewLayout(panel,params); return true;
                }
                return true;
            }
        });
        // Canvas handles its own touches; title/outer panel can be dragged.
        wm.addView(panel, params);
    }
    private void addButton(LinearLayout row, String text, Runnable action) {
        Button b = new Button(this); b.setText(text); b.setTextSize(12);
        b.setOnClickListener(v -> action.run()); row.addView(b);
    }
    @Override public int onStartCommand(Intent i,int flags,int id){ return START_NOT_STICKY; }
    @Override public void onDestroy(){ if(panel!=null) wm.removeView(panel); super.onDestroy(); }
    @Override public IBinder onBind(Intent i){ return null; }

    class AimView extends View {
        Paint p = new Paint(3); int mode=0; PointF s,t,k;
        AimView(android.content.Context c){ super(c); setBackgroundColor(0x55333333); }
        void setMode(int m){mode=m; invalidate();}
        @Override protected void onDraw(Canvas c){
            super.onDraw(c);
            p.setColor(Color.WHITE); p.setTextSize(13); c.drawText("Tap here to place " + (mode==0?"STRIKER":mode==1?"TARGET":"POCKET"),8,18,p);
            if(s!=null && t!=null){p.setColor(Color.WHITE);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);p.setPathEffect(new DashPathEffect(new float[]{8,7},0));c.drawLine(s.x,s.y,t.x,t.y,p);p.setPathEffect(null);p.setColor(0xFFFFD43B);c.drawLine(s.x,s.y,t.x,t.y,p);p.setStyle(Paint.Style.FILL);}
            if(t!=null && k!=null){p.setColor(0xFF43E8E0);p.setStrokeWidth(3);c.drawLine(t.x,t.y,k.x,k.y,p);}
            mark(c,s,0xFFFFFF00,"S");mark(c,t,0xFFFF4545,"T");mark(c,k,0xFF43E8E0,"P");
        }
        void mark(Canvas c,PointF a,int color,String label){if(a==null)return;p.setColor(color);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(3);c.drawCircle(a.x,a.y,9,p);p.setStyle(Paint.Style.FILL);c.drawText(label,a.x+10,a.y-8,p);}
        @Override public boolean onTouchEvent(android.view.MotionEvent e){
            if(e.getAction()==MotionEvent.ACTION_UP){PointF q=new PointF(e.getX(),e.getY());if(mode==0)s=q;else if(mode==1)t=q;else k=q;invalidate();return true;}return true;
        }
    }
}
