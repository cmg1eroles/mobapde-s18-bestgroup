package ph.edu.dlsu.mobapde.tara;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by louis on 12/13/2017.
 */

public class AddUserDialog extends DialogFragment {
    View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_adduser, null);

        AlertDialog.Builder dialogBuilder
                = new AlertDialog.Builder(getActivity())
                .setTitle("Add")
                .setMessage("Enter username of person you want to add")
                .setView(v)
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dismiss();
                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dismiss();

                        EditText etUsername = (EditText) v.findViewById(R.id.et_addusername);

                        ((AddUsersActivity) getActivity()).addUserToRace(etUsername.getText().toString());
                    }
                });

        return dialogBuilder.create();
    }
}
