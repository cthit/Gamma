import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

type ShowPostLoaderReturn = Awaited<ReturnType<typeof showPostLoader>>;

export const useShowPostLoaderData = (): ShowPostLoaderReturn => {
  return useLoaderData() as ShowPostLoaderReturn;
};

export const showPostLoader = async (id: string) => {
  return await GammaClient.instance().posts.getPost(id);
};
