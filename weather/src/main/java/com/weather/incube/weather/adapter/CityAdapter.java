package com.weather.incube.weather.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.weather.incube.weather.R;
import com.weather.incube.weather.model.City;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pankaj on 16-12-2017.
 */

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> implements Filterable {

    private Context context;
    private List<City> cityList;
    private List<City> cityListFiltered;
    private CityAdapterListener listener;

    public CityAdapter(Context context, List<City> cityList, CityAdapterListener listener){
        this.context = context;
        this.cityList = cityList;
        this.cityListFiltered = cityList;
        this.listener = listener;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    cityListFiltered = cityList;
                } else {
                    List<City> filteredList = new ArrayList<>();
                    for (City row : cityList) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase())
                                || row.getCountry().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }
                    cityListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = cityListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                cityListFiltered = (ArrayList<City>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class CityViewHolder extends RecyclerView.ViewHolder {
        public TextView cityName, countryName;

        public CityViewHolder(View view) {
            super(view);
            cityName = view.findViewById(R.id.cityName);
            countryName = view.findViewById(R.id.countryName);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onCitySelected(cityListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }

    @Override
    public CityViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_list_item, parent, false);

        return new CityViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CityViewHolder holder, final int position) {
        final City contact = cityListFiltered.get(position);
        holder.cityName.setText(contact.getName());
        holder.countryName.setText(contact.getCountry());
    }

    @Override
    public int getItemCount() {
        return cityListFiltered.size();
    }

    public interface CityAdapterListener {
        void onCitySelected(City city);
    }
}
