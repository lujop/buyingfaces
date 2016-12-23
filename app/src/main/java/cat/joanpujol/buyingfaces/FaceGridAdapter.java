package cat.joanpujol.buyingfaces;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import cat.joanpujol.buyingfaces.data.FaceProduct;
import cat.joanpujol.buyingfaces.util.DpUtils;

/**
 * Created by lujop on 22/12/2016.
 */

public class FaceGridAdapter extends RecyclerView.Adapter<FaceGridAdapter.FaceViewHolder> {
    public static final float  SMALL_CARD_HEIGHT = 150f;
    public static final float  NORMAL_CARD_HEIGHT = 200f;
    public static final float  BIG_CARD_HEIGHT = 250f;
    private List<FaceProduct> list = new ArrayList<>();

    public void clearResults() {
        list.clear();
        notifyDataSetChanged();
    }

    public void addResults(List<FaceProduct> results) {
        int insertedAt = list.size();
        list.addAll(results);
        notifyItemRangeInserted(insertedAt,results.size());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public FaceGridAdapter.FaceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.face_card,parent,false);
        return new FaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FaceGridAdapter.FaceViewHolder holder, int position) {
        FaceProduct product = list.get(position);
        if(product!=null)
            holder.bind(product);
    }

    public class FaceViewHolder extends  RecyclerView.ViewHolder{
        private FaceProduct faceProduct;
        private TextView faceText;
        private TextView pricing;
        private DecimalFormat decimalFormat = new DecimalFormat("#.##");

        public FaceViewHolder(View itemView) {
            super(itemView);
            faceText = (TextView) itemView.findViewById(R.id.faceText);
            pricing = (TextView) itemView.findViewById(R.id.pricing);
        }

        public void bind(FaceProduct product) {
            faceProduct = product;
            faceText.setText(product.getFace());
            String price = "";
            if(price!=null)
                price = "$" + decimalFormat.format(product.getPrice());
            pricing.setText(price);

            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            if(product.getSize()<=15) {
                params.height = (int) DpUtils.convertDpToPixel(SMALL_CARD_HEIGHT,itemView.getContext());
            } else if(product.getSize()<=25) {
                params.height = (int) DpUtils.convertDpToPixel(NORMAL_CARD_HEIGHT,itemView.getContext());
            } else {
                params.height = (int) DpUtils.convertDpToPixel(BIG_CARD_HEIGHT,itemView.getContext());
            }
            itemView.setLayoutParams(params);
        }
    }
}
