package com.shortstack.todozzz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.shortstack.R;

public class ToDoZzz extends Activity
{
    /** Called when the todozzz is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // button for To Do List, switches to List Activity

        Button listButton = (Button)findViewById(R.id.toDoList);
        listButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(ToDoZzz.this,
                        List.class));
            }
        });

        // button for Naps, switches to Naps Activity

        Button napsButton = (Button)findViewById(R.id.napList);
        napsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(ToDoZzz.this,
                        Naps.class));
            }
        });
    }
}
