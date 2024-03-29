package com.example.rkhadka.hw2017mobiledevlab5;

import  android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

public class WikipediaArticleListFragment extends Fragment {
    private ListView mListView;
    private ArticleAdapter mArticleAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NetworkImageView imageView;
    private TextView titleTextView;
    private TextView bodyTextView;
    private List<Article> mArticles;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wikepediaarticlelist, container, false);

        // Set up the SwipeRefreshLayout to reload when swiped.
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshArticles();
            }
        });

        // Set up the list view.
        mListView = (ListView) v.findViewById(R.id.list_view);
        mArticleAdapter = new ArticleAdapter(getActivity());
        mListView.setAdapter(mArticleAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article article = (Article) parent.getAdapter().getItem(position);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(article.getURLString()));
                startActivity(i);
                // Add your click handling code here.
            }
        });

        // If there is content to display, show it, otherwise refresh content.
        if (mArticles != null) {
            mArticleAdapter.setItems(mArticles);
        } else {
            mSwipeRefreshLayout.setRefreshing(true);
            refreshArticles();
        }

        return v;
    }

    private void refreshArticles() {
        WikipediaArticleSource.get(getContext()).getArticles(new WikipediaArticleSource.ArticleListener() {
            @Override
            public void onArticleResponse(List<Article> articleList) {
                mArticles = articleList;
                // Stop the spinner and update the list view.
                mSwipeRefreshLayout.setRefreshing(false);
                mArticleAdapter.setItems(articleList);
            }
        });
    }

    private class ArticleAdapter extends BaseAdapter {
        private Context mContext;
        private LayoutInflater mInflater;
        private List<Article> mDataSource;

        public ArticleAdapter(Context context) {
            mContext = context;
            mDataSource = new ArrayList<>();
            mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void setItems(List<Article> articleList) {
            mDataSource.clear();
            mDataSource.addAll(articleList);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mDataSource.size();
        }

        @Override
        public Object getItem(int position) {
            return mDataSource.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get model item for this row, and generate view for this row.
            final Article article = mDataSource.get(position);
            View rowView = mInflater.inflate(R.layout.list_item_article, parent, false);

            // Add your code here. You'll want to:
            // 1) Get local references to the title TextView and body TextView, and set the
            // article's contents on them.
            // 2) Get a reference to the NetworkImageView, and use the ImageLoader vended by
            // WikipediaArticleSource to set the image.
            imageView = (NetworkImageView) rowView.findViewById(R.id.thumbnail);
            titleTextView = (TextView) rowView.findViewById(R.id.cell_title);
            bodyTextView = (TextView) rowView.findViewById(R.id.body_text);
            titleTextView.setText(article.getTitle());
            bodyTextView.setText(article.getBodyExtract());
            ImageLoader loader = WikipediaArticleSource.get(getContext()).getImageLoader();
            imageView.setImageUrl(article.getImageURLString(), loader);
            return rowView;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
}
