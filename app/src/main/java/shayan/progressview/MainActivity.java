package shayan.progressview;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.main_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(new SimpleAdapter());

    }

    class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.SimpleViewHolder> {


        LayoutInflater inflater;

        SimpleAdapter() {
            inflater = (LayoutInflater) MainActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new SimpleViewHolder(inflater.inflate(R.layout.row, parent, false));
        }

        @Override
        public void onBindViewHolder(SimpleViewHolder holder, int position) {
            holder.onBind();
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        class SimpleViewHolder extends RecyclerView.ViewHolder {

            CircularProgress circularProgress;
            Handler handler;

            SimpleViewHolder(View itemView) {
                super(itemView);
                circularProgress = (CircularProgress) itemView.findViewById(R.id.row_circularProgress);
                circularProgress.setProgress(getAdapterPosition());
                handler = new Handler(Looper.getMainLooper());
            }

            void onBind() {
                circularProgress.setProgressNum((getAdapterPosition() % 8) + 1);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        circularProgress.setProgress(circularProgress.getProgress() + 1);
                        handler.postDelayed(this, 500);
                    }
                }, 500);
            }
        }

    }

}
