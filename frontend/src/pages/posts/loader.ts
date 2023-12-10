import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma-client";

type PostsLoaderReturn = Awaited<ReturnType<typeof postsLoader>>;

export const usePostsLoaderData = (): PostsLoaderReturn => {
  return useLoaderData() as PostsLoaderReturn;
};

export const postsLoader = async () => {
  return await GammaClient.getInstance().posts.getPosts();
};
