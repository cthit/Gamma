import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

type CreateClientsLoaderReturn = Awaited<
  ReturnType<typeof createClientsLoader>
>;

export const useCreateClientsLoaderData = (): CreateClientsLoaderReturn => {
  return useLoaderData() as CreateClientsLoaderReturn;
};

export const createClientsLoader = async () => {
  return {
    superGroups: await GammaClient.instance().superGroups.getSuperGroups(),
    users: await GammaClient.instance().users.getUsers(),
    posts: await GammaClient.instance().posts.getPosts(),
  };
};
