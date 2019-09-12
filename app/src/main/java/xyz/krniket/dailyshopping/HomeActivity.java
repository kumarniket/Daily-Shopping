package xyz.krniket.dailyshopping;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseIndexRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import xyz.krniket.dailyshopping.DataPart.Data;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private FloatingActionButton fab_btn;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    //for item data layout
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Daily Shopping List");

        mAuth=FirebaseAuth.getInstance();

        //current user uid from firebase
        FirebaseUser muser=mAuth.getCurrentUser();
        String uid=muser.getUid();

        //join firebase data base
        mDatabase= FirebaseDatabase.getInstance().getReference().child("shopping List").child(uid);

        mDatabase.keepSynced(true);

        recyclerView=findViewById(R.id.recycler_home);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);

        //for connecting data to firebase sync
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        fab_btn=findViewById(R.id.fab);

        fab_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog();

            }
        });


    }
//here we design custom dialog
    private void customDialog(){
        //call the custome dialog

        AlertDialog.Builder mydialog=new AlertDialog.Builder(HomeActivity.this);

        LayoutInflater inflater=LayoutInflater.from(HomeActivity.this);

        View myview = inflater.inflate(R.layout.input_data,null);

        final AlertDialog dialog=mydialog.create();

        dialog.setView(myview);


        final EditText type=myview.findViewById(R.id.et_type);
        final EditText amount=myview.findViewById(R.id.et_amount);
        final EditText note=myview.findViewById(R.id.et_note);

        Button Save=myview.findViewById(R.id.btn_save);

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mType=type.getText().toString().trim();
                String mamount=amount.getText().toString().trim();
                String mNote=note.getText().toString().trim();

                //set data convert into integer
                int amountint=Integer.parseInt(mamount);

                if(TextUtils.isEmpty(mType)){
                    type.setError("Required");
                    return;
                }

                if(TextUtils.isEmpty(mamount)){
                    amount.setError("Required");
                    return;
                }

                if(TextUtils.isEmpty(mNote)){
                    note.setError("Required");
                    return;
                }

                //generate random id
                String id=mDatabase.push().getKey();

                String date= DateFormat.getDateInstance().format(new Date());
                //pass data in data.java
                Data data=new Data(mType,amountint,mNote,date,id);

                //pass id in database
                mDatabase.child(id).setValue(data);

                Toast.makeText(getApplicationContext(),"Shopping list updated..",Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();

        //time to add firebase recycler adapter


        FirebaseRecyclerAdapter<Data,MyViewHolder>adapter=new FirebaseIndexRecyclerAdapter<Data, MyViewHolder>
                (
                        Data.class,
                        R.layout.item_data,
                        MyViewHolder.class,
                        mDatabase

                )
        {
            @Override
            protected void populateViewHolder(MyViewHolder myViewHolder, Data data, int i) {
                myViewHolder.setDate(data.getDate());
                myViewHolder.setType(data.getType());
                myViewHolder.setNote(data.getNote());
                myViewHolder.setAmount(data.getAmount());

            }
        };

        recyclerView.setAdapter(adapter);

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View myview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview=itemView;


        }

        public void setType(String type){
            TextView mType=myview.findViewById(R.id.type);
            mType.setText(type);
        }

        public void setNote(String note){
            TextView mNote=myview.findViewById(R.id.note);
            mNote.setText(note);
        }

        public void setDate(String date){
            TextView mDate=myview.findViewById(R.id.date);
            mDate.setText(date);
        }

        public void setAmount(int amount){
            TextView mAmount=myview.findViewById(R.id.amount);

            String stam=String.valueOf(amount);

            mAmount.setText(stam);
        }


    }
}
