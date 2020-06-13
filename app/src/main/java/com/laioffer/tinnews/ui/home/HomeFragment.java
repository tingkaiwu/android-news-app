package com.laioffer.tinnews.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.laioffer.tinnews.R;
import com.laioffer.tinnews.databinding.FragmentHomeBinding;
import com.laioffer.tinnews.model.Article;
import com.laioffer.tinnews.repository.NewsRepository;
import com.laioffer.tinnews.repository.NewsViewModelFactory;
import com.mindorks.placeholderview.SwipeDecor;

public class HomeFragment extends Fragment implements TinNewsCard.OnSwipeListener {
    private HomeViewModel viewModel;
    private FragmentHomeBinding binding;

    @Override
    public void onLike(Article news) {
        viewModel.setFavoriteArticleInput(news);
    }

    @Override
    public void onDisLike(Article news) {
        if (binding.swipeView.getChildCount() < 3) {
            viewModel.setCountryInput("us");
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.onCancel();
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding
                .swipeView
                .getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(
                        new SwipeDecor()
                                .setPaddingTop(20)
                                .setRelativeScale(0.01f));

        binding.rejectBtn.setOnClickListener(v -> binding.swipeView.doSwipe(false));
        binding.acceptBtn.setOnClickListener(v -> binding.swipeView.doSwipe(true));

        NewsRepository repository = new NewsRepository(getContext());
        viewModel = new ViewModelProvider(this, new NewsViewModelFactory(repository))
                .get(HomeViewModel.class);
        viewModel.setCountryInput("us");
        viewModel
                .getTopHeadlines()
                .observe(
                        getViewLifecycleOwner(),
                        newsResponse -> {
                            if (newsResponse != null) {
                                for (Article article : newsResponse.articles) {
                                    TinNewsCard tinNewsCard = new TinNewsCard(article, this);
                                    binding.swipeView.addView(tinNewsCard);
                                }
                            }
                        });
        viewModel
                .onFavorite()
                .observe(
                        getViewLifecycleOwner(),
                        isSuccess -> {
                            if (isSuccess) {
                                Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "You might have liked before", Toast.LENGTH_SHORT).show();
                            }
                        });
    }
}