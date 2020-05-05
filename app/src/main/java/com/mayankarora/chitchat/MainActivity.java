package com.mayankarora.chitchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import androidx.appcompat.widget.Toolbar;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private ViewPager viewpager;
    private SectionPagerAdapter sectionpageradapter;
    private TabLayout tablayout;
    private DatabaseReference userDatabase;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mtoolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("ChitChat");
        //Tabs
        viewpager=(ViewPager)findViewById(R.id.main_tabpager);
        sectionpageradapter=new SectionPagerAdapter(getSupportFragmentManager());
        viewpager.setAdapter(sectionpageradapter);

        tablayout=(TabLayout)findViewById(R.id.main_tabs);
        tablayout.setupWithViewPager(viewpager);

        if(mAuth.getCurrentUser()!=null)
        {
            userDatabase= FirebaseDatabase.getInstance().getReference()
                    .child("users").child(mAuth.getCurrentUser().getUid());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            sendtostart();
        }
        else{
            userDatabase.child("online").setValue("true");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(currentUser!=null)
        {
            userDatabase.child("online").setValue(DateFormat.getDateTimeInstance().format(new Date()));
        }

    }

    private void sendtostart() {
        Intent startintent=new Intent(MainActivity.this, startactivity.class);
        startActivity(startintent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId()==R.id.main_logout_btn){
            FirebaseAuth.getInstance().signOut();
            sendtostart();
        }
        if(item.getItemId()==R.id.main_settingsbtn){
            Intent settingsIntent=new Intent(MainActivity.this,AccountSettings.class);
            startActivity(settingsIntent);
        }
        if(item.getItemId()==R.id.main_allusersbtn){
            Intent allusersIntent=new Intent(MainActivity.this,AllusersActivity.class);
            startActivity(allusersIntent);
        }
        return true;
    }
}
