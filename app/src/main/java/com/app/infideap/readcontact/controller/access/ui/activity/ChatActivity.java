package com.app.infideap.readcontact.controller.access.ui.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.app.infideap.readcontact.R;
import com.app.infideap.readcontact.controller.access.ui.fragment.ChatFragment;
import com.app.infideap.readcontact.entity.Chat;
import com.app.infideap.readcontact.entity.Contact;
import com.app.infideap.readcontact.util.Common;
import com.app.infideap.readcontact.util.Constant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends BaseActivity implements
        ChatFragment.OnListFragmentInteractionListener {

    public static final String CONTACT = "CONTACT";
    private EditText messageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Contact contact = (Contact) getIntent().getSerializableExtra(CONTACT);

        if (contact == null)
            finish();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(contact.name);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        messageEditText = (EditText) findViewById(R.id.editText_message);

    }

    @Override
    public void onListFragmentInteraction(Chat chat) {

    }

    public void send(View view) {
        final String message = messageEditText.getText().toString();
        if (message.trim().length() == 0) {
            return;
        }
        final Contact contact = (Contact) getIntent()
                .getSerializableExtra(ChatActivity.CONTACT);
        database.getReference(Constant.USER).child(Common.getSimSerialNumber(this))
                .child(Constant.PHONE_NUMBER)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String phoneNumber =
                                dataSnapshot.getValue().toString();
                        if (phoneNumber == null)
                            return;


                        database.getReference(Constant.CHAT)
                                .child(Common.convertToChatKey(phoneNumber, contact.phoneNumber))
                                .push()
                                .setValue(
                                        new Chat(message, phoneNumber, System.currentTimeMillis())
                                ).addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        messageEditText.setText(null);
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
}
