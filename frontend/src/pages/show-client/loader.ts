import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

type ShowClientLoaderReturn = Awaited<ReturnType<typeof showClientLoader>>;

export const useShowClientLoaderData = (): ShowClientLoaderReturn => {
  return useLoaderData() as ShowClientLoaderReturn;
};

export const showClientLoader = async (id: string) => {
  return {
    client: await GammaClient.instance().clients.getClient(id),
    authorities:
      await GammaClient.instance().clientAuthorities.getAuthorities(id),
    superGroups: await GammaClient.instance().superGroups.getSuperGroups(),
    users: await GammaClient.instance().users.getUsers(),
    posts: await GammaClient.instance().posts.getPosts(),
  };
};
