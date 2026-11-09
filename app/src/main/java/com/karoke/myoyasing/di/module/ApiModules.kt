package com.karoke.myoyasing.di.module

import com.google.gson.Gson
import com.karoke.myoyasing.data.remote.LoginApiService
import com.karoke.myoyasing.data.repository.CreatePerformanceRepoImpl
import com.karoke.myoyasing.data.repository.FeedRepositoryImpl
import com.karoke.myoyasing.data.repository.HashtagRepositoryImpl
import com.karoke.myoyasing.data.repository.LoginRepositoryImpl
import com.karoke.myoyasing.data.repository.MediaRepositoryImpl
import com.karoke.myoyasing.data.repository.MelodyRepositoryImpl
import com.karoke.myoyasing.data.repository.OtpVerificationRepositoryImpl
import com.karoke.myoyasing.data.repository.PerformanceCommentsRepositoryImpl
import com.karoke.myoyasing.data.repository.PerformanceLikeRepositoryImpl
import com.karoke.myoyasing.data.repository.PerformanceRepositoryImpl
import com.karoke.myoyasing.data.repository.SharePerformanceRepositoryImpl
import com.karoke.myoyasing.data.repository.SongsRepositoryImpl
import com.karoke.myoyasing.data.repository.UserFollowRepositoryImpl
import com.karoke.myoyasing.di.network.MainRetrofitClient
import com.karoke.myoyasing.domain.repo.CreatePerformanceRepo
import com.karoke.myoyasing.domain.repo.FeedRepository
import com.karoke.myoyasing.domain.repo.HashtagRepository
import com.karoke.myoyasing.domain.repo.LoginRepository
import com.karoke.myoyasing.domain.repo.MediaRepository
import com.karoke.myoyasing.domain.repo.MelodyRepository
import com.karoke.myoyasing.domain.repo.OtpVerificationRepository
import com.karoke.myoyasing.domain.repo.PerformanceCommentsRepository
import com.karoke.myoyasing.domain.repo.PerformanceLikeRepository
import com.karoke.myoyasing.domain.repo.PerformanceRepository
import com.karoke.myoyasing.domain.repo.SharePerformanceRepository
import com.karoke.myoyasing.domain.repo.SongsRepository
import com.karoke.myoyasing.domain.repo.UserFollowRepository
import com.karoke.myoyasing.domain.usecase.AddPerformanceCommentUseCase
import com.karoke.myoyasing.domain.usecase.CompareMelodyUseCase
import com.karoke.myoyasing.domain.usecase.CreatePerformanceUseCase
import com.karoke.myoyasing.domain.usecase.DeletePerformanceCommentUseCase
import com.karoke.myoyasing.domain.usecase.FollowUserUseCase
import com.karoke.myoyasing.domain.usecase.GetFeedUseCase
import com.karoke.myoyasing.domain.usecase.GetPerformanceCommentsUseCase
import com.karoke.myoyasing.domain.usecase.GetSongByIdUseCase
import com.karoke.myoyasing.domain.usecase.GetSongLyricsUseCase
import com.karoke.myoyasing.domain.usecase.GetSongsUseCase
import com.karoke.myoyasing.domain.usecase.LikePerformanceUseCase
import com.karoke.myoyasing.domain.usecase.LoginUseCase
import com.karoke.myoyasing.domain.usecase.PublishPerformanceUseCase
import com.karoke.myoyasing.domain.usecase.SearchHashtagsUseCase
import com.karoke.myoyasing.domain.usecase.SharePerformanceUseCase
import com.karoke.myoyasing.domain.usecase.UnfollowUserUseCase
import com.karoke.myoyasing.domain.usecase.UnlikePerformanceUseCase
import com.karoke.myoyasing.domain.usecase.UploadMediaUseCase
import com.karoke.myoyasing.domain.usecase.VerifyOtpUseCase
import com.karoke.myoyasing.presentation.featureUI.auth.login.LoginViewModel
import com.karoke.myoyasing.presentation.featureUI.auth.otp.OtpVerificationViewModel
import com.karoke.myoyasing.presentation.featureUI.home.SongsViewModel
import com.karoke.myoyasing.presentation.featureUI.recording.MediaUploadViewModel
import com.karoke.myoyasing.presentation.featureUI.recording.MelodyComparisonViewModel
import com.karoke.myoyasing.presentation.featureUI.recording.SongDetailViewModel
import com.karoke.myoyasing.presentation.feed.viewmodel.FeedViewModel
import com.karoke.myoyasing.presentation.feed.viewmodel.PerformanceCommentsViewModel
import com.karoke.myoyasing.presentation.feed.viewmodel.PerformanceLikeViewModel
import com.karoke.myoyasing.presentation.feed.viewmodel.SharePerformanceViewModel
import com.karoke.myoyasing.presentation.feed.viewmodel.UserFollowViewModel
import com.karoke.myoyasing.presentation.publishing.viewmodels.CreatePerformanceViewModel
import com.karoke.myoyasing.presentation.publishing.viewmodels.HashtagViewModel
import com.karoke.myoyasing.presentation.publishing.viewmodels.PublishPerformanceViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val ApiModules = module {

    single { Gson() }
    single<LoginApiService> { MainRetrofitClient.Loginapi() }
    single<LoginApiService> { MainRetrofitClient.getAllSongs() }
    single<LoginApiService> { MainRetrofitClient.getSongDetails() }
    single<LoginApiService> { MainRetrofitClient.UploadSongs() }
    single<LoginApiService> { MainRetrofitClient.createPerformance() }
    single<LoginApiService> { MainRetrofitClient.calculateScore() }
    single<LoginApiService> { MainRetrofitClient.getHashtags() }
    single<LoginApiService> { MainRetrofitClient.publishPerformances() }
    single<LoginApiService> { MainRetrofitClient.getFeeds() }

    single<LoginApiService> { MainRetrofitClient.sharePerformances() }
    single<LoginApiService> { MainRetrofitClient.LikePerformances() }
    single<LoginApiService> { MainRetrofitClient.DislikePerformances() }
    single<LoginApiService> { MainRetrofitClient.getPerformanceComments() }
    single<LoginApiService> { MainRetrofitClient.AddPerformanceComments()}
    single<LoginApiService> { MainRetrofitClient.DeletePerformanceComments() }
    single<LoginApiService> { MainRetrofitClient.FollowUser() }
    single<LoginApiService> { MainRetrofitClient.UnfollowUser() }


    single<LoginRepository> {
        LoginRepositoryImpl(get())
    }
    factory { LoginUseCase(get()) }
    viewModel { LoginViewModel(get()) }


    single<OtpVerificationRepository> {
        OtpVerificationRepositoryImpl(
            apiService = get()
        )
    }

    factory {
        VerifyOtpUseCase(
            repository = get()
        )
    }

    viewModel {
        OtpVerificationViewModel(
            verifyOtpUseCase = get(), get()
        )
    }
    single<SongsRepository> { SongsRepositoryImpl(get()) }
    factory { GetSongsUseCase(get()) }
    viewModel { SongsViewModel(get()) }

    factory {
        GetSongByIdUseCase(repository = get())
    }

    factory {
        GetSongLyricsUseCase(repository = get())
    }

    viewModel {
        SongDetailViewModel(
            getSongByIdUseCase = get(),
            getSongLyricsUseCase = get()
        )
    }
    single<MediaRepository> { MediaRepositoryImpl(get()) }
    single { UploadMediaUseCase(get()) }
    viewModel { MediaUploadViewModel(get()) }

    single<CreatePerformanceRepo> { CreatePerformanceRepoImpl(get()) }
    factory { CreatePerformanceUseCase(get()) }
    viewModel { CreatePerformanceViewModel(get()) }

    single<MelodyRepository> {
        MelodyRepositoryImpl(
            apiService = get()
        )
    }
    factory {
        CompareMelodyUseCase(
            repository = get()
        )
    }

    viewModel {
        MelodyComparisonViewModel(
            compareMelodyUseCase = get()
        )
    }

    single<HashtagRepository> {
        HashtagRepositoryImpl(
            apiService = get()
        )
    }

    factory {
        SearchHashtagsUseCase(
            repository = get()
        )
    }

    viewModel {
        HashtagViewModel(
            searchHashtagsUseCase = get()
        )
    }
    single<PerformanceRepository> {
        PerformanceRepositoryImpl(
            apiService = get()
        )
    }


    factory {
        PublishPerformanceUseCase(
            repository = get()
        )
    }

    viewModel {
        PublishPerformanceViewModel(
            publishPerformanceUseCase = get()
        )
    }
    single<FeedRepository> {
        FeedRepositoryImpl(
            apiService = get()
        )
    }

    factory {
        GetFeedUseCase(
            repository = get()
        )
    }


    viewModel {
        FeedViewModel(
            getFeedUseCase = get()
        )
    }

    single<SharePerformanceRepository> {
        SharePerformanceRepositoryImpl(get())
    }


    factory {
        SharePerformanceUseCase(get())
    }

    viewModel {
        SharePerformanceViewModel(get())
    }

    single<PerformanceLikeRepository> {
        PerformanceLikeRepositoryImpl(get())
    }

    factory {
        LikePerformanceUseCase(get())
    }

    factory {
        UnlikePerformanceUseCase(get())
    }


    viewModel {
        PerformanceLikeViewModel(
            likePerformanceUseCase = get(),
            unlikePerformanceUseCase = get()
        )
    }

    single<PerformanceCommentsRepository> {
        PerformanceCommentsRepositoryImpl(get())
    }


    factory {
        GetPerformanceCommentsUseCase(get())
    }

    viewModel {
        PerformanceCommentsViewModel(get(),get(),get())
    }

    single<PerformanceCommentsRepository> {
        PerformanceCommentsRepositoryImpl(get())
    }

    factory {
        GetPerformanceCommentsUseCase(get())
    }

    factory {
        AddPerformanceCommentUseCase(get())
    }

    factory {
        DeletePerformanceCommentUseCase(get())
    }

    viewModel {
        PerformanceCommentsViewModel(
            getPerformanceCommentsUseCase = get(),
            addPerformanceCommentUseCase = get(),
            deletePerformanceCommentUseCase = get()
        )
    }

    single<UserFollowRepository> {
        UserFollowRepositoryImpl(get())
    }


    factory {
        FollowUserUseCase(get())
    }

    factory {
        UnfollowUserUseCase(get())
    }


    viewModel {
        UserFollowViewModel(
            followUserUseCase = get(),
            unfollowUserUseCase = get()
        )
    }
}

