package slack.cl.com.mytestapplication.camera;

import android.content.Context;
import android.hardware.Camera;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;

import slack.cl.com.mytestapplication.R;

/**
 * <p>Description: 相册列表 </p>
 * Created by slack on 2016/9/6 16:12 .
 */
public class CameraPreviewSizeAdapter extends RecyclerView.Adapter<CameraPreviewSizeAdapter.ViewHolder> {

    private List<Camera.Size> data;
    private Context mContext;
    private ItemClickListener mItemClickListener;

    public CameraPreviewSizeAdapter(Context context,List<Camera.Size> list,ItemClickListener itemClickListener) {
        mContext = context;
        data = list;
        mItemClickListener = itemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.preview_camera, parent, false));
    }

    @Override
    public void onBindViewHolder(CameraPreviewSizeAdapter.ViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Button mButton;

        public ViewHolder(View itemView) {
            super(itemView);
            mButton = ((Button)itemView.findViewById(R.id.button));
            mButton.setOnClickListener(this);
        }

        public void bindView(int position) {
            mButton.setText(data.get(position).width + " * " + data.get(position).height);
        }

        @Override
        public void onClick(View view) {
            if(mItemClickListener != null){
                mItemClickListener.onItemClick(getAdapterPosition());
            }
        }
    }

    public interface ItemClickListener{
        void onItemClick(int pos);
    }

}
