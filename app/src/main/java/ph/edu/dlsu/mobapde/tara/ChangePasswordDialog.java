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

public class ChangePasswordDialog extends DialogFragment {
    View v;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_changepassword, null);

        AlertDialog.Builder dialogBuilder
                = new AlertDialog.Builder(getActivity())
                .setTitle("Change Password")
                .setView(v)
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dismiss();
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dismiss();

                        EditText etPassword = (EditText) v.findViewById(R.id.et_oldpassword);
                        EditText etNewPassword = (EditText) v.findViewById(R.id.et_newpassword);
                        EditText etConPassword = (EditText) v.findViewById(R.id.et_confirmpassword);

                        ((SettingsActivity) getActivity()).changePassword(etPassword.getText().toString(),
                                etNewPassword.getText().toString(), etConPassword.getText().toString());
                    }
                });

        return dialogBuilder.create();
    }
}
