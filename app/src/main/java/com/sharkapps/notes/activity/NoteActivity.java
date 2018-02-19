package com.sharkapps.notes.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.sharkapps.notes.BuildConfig;
import com.sharkapps.notes.R;
import com.sharkapps.notes.db.OpenHelper;
import com.sharkapps.notes.fragment.DrawingNoteFragment;
import com.sharkapps.notes.fragment.SimpleNoteFragment;
import com.sharkapps.notes.fragment.template.NoteFragment;
import com.sharkapps.notes.model.Category;
import com.sharkapps.notes.model.DatabaseModel;

public class NoteActivity extends AppCompatActivity implements NoteFragment.Callbacks {
	public static final int REQUEST_CODE = 2;
	public static final int RESULT_NEW = 101;
	public static final int RESULT_EDIT = 102;
	public static final int RESULT_DELETE = 103;

	private int noteResult = 0;
	private int position;
    private AdView mAdView;

	private NoteFragment fragment;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		Intent data = getIntent();
		setTheme(Category.getStyle(data.getIntExtra(OpenHelper.COLUMN_THEME, Category.THEME_GREEN)));
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_note);

		position = data.getIntExtra("position", 0);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		try {
			//noinspection ConstantConditions
			getSupportActionBar().setDisplayShowTitleEnabled(false);
		} catch (Exception ignored) {
		}

		toolbar.findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onBackPressed();
			}
		});

		if (savedInstanceState == null) {
			if (data.getIntExtra(OpenHelper.COLUMN_TYPE, DatabaseModel.TYPE_NOTE_SIMPLE) == DatabaseModel.TYPE_NOTE_SIMPLE) {
				fragment = new SimpleNoteFragment();
			} else {
				fragment = new DrawingNoteFragment();
			}

			getSupportFragmentManager().beginTransaction()
				.add(R.id.container, fragment)
				.commit();
		}
		if(!BuildConfig.FULL_VERSION){
            mAdView = findViewById(R.id.adView);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);


		}

	}

	@Override
	public void onBackPressed() {
		fragment.saveNote(new NoteFragment.SaveListener() {
			@Override
			public void onSave() {
				final Intent data = new Intent();
				data.putExtra("position", position);
				data.putExtra(OpenHelper.COLUMN_ID, fragment.note.id);

				switch (noteResult) {
					case RESULT_NEW:
						data.putExtra(OpenHelper.COLUMN_TYPE, fragment.note.type);
						data.putExtra(OpenHelper.COLUMN_DATE, fragment.note.createdAt);
					case RESULT_EDIT:
						data.putExtra(OpenHelper.COLUMN_TITLE, fragment.note.title);
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						setResult(noteResult, data);
						finish();
					}
				});
			}
		});
	}

	@Override
	public void setNoteResult(int result, boolean closeActivity) {
		noteResult = result;
		if (closeActivity) {
			Intent data = new Intent();
			data.putExtra("position", position);
			data.putExtra(OpenHelper.COLUMN_ID, fragment.note.id);
			setResult(result, data);
			finish();
		}
	}
}
