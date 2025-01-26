package com.example.smarthome;

import retrofit2.http.POST;

public interface GitHubService {
  @GET("users/{user}/repos")
  Call<List<Repo>> listRepos(@Path("user") String user);

  @POST
  Call<List<Repo>> listRepos(@Path("user") String user);

}