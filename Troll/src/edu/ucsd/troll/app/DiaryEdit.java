package edu.ucsd.troll.app;

import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DiaryEdit extends Activity{

	private EditText mTitleText;
	private EditText mBodyText;
	private Long mRowId;
	private NotesDbAdapter mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new NotesDbAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.note_edit);
		setTitle(R.string.diary_edit_note);

		mTitleText = (EditText) findViewById(R.id.diary_title);
		mBodyText  = (EditText) findViewById(R.id.diary_body);

		final Button confirmButton = (Button) findViewById(R.id.diary_confirm);
		//        final Button deleteButton  = (Button) findViewById(R.id.diary_deleteBtn);

		mRowId = (savedInstanceState == null) ? null :
			(Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
					: null;
		}
		Typeface btn_font = Typeface.createFromAsset(getAssets(), "KaushanScript-Regular.ttf");
		confirmButton.setTypeface(btn_font);
		//        deleteButton.setTypeface(btn_font);

		// Set Button Shape
		Drawable round_btn = getResources().getDrawable(R.drawable.round_btn);
		confirmButton.setBackgroundDrawable(round_btn);
		//        deleteButton.setBackgroundDrawable(round_btn);

		populateFields();

		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}
		});

		//       	deleteButton.setOnClickListener(new View.OnClickListener() {
		//
		//			@Override
		//			public void onClick(View v) {
		//				if(mDbHelper != null){
		//					mDbHelper.deleteNote(R.id.diary_deleteBtn);
		//					//fillData();
		//				}
		//			}
		//		});
	}

	@SuppressWarnings("deprecation")
	private void populateFields() {
		if (mRowId != null) {
			Cursor note = mDbHelper.fetchNote(mRowId);
			startManagingCursor(note);
			mTitleText.setText(note.getString(
					note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
			mBodyText.setText(note.getString(
					note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mDbHelper != null){
			saveState();
			mDbHelper.close();
			mDbHelper = null;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
		if(mDbHelper == null){
			mDbHelper.open();
		}
	}

	private void saveState() {
		if(mDbHelper != null && mTitleText != null && mBodyText != null){
			String title = mTitleText.getText().toString();
			String body  = mBodyText.getText().toString();
			if(title != null && title != "" && body != null && body != ""){
				if (mRowId == null) {
					long id = mDbHelper.createNote(title, body);
					if (id > 0) {
						mRowId = id;
					}
				} else {
					mDbHelper.updateNote(mRowId, title, body);
				}
			}
		}
	}


}
