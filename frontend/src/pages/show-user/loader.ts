import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

type ShowUserLoaderReturn = Awaited<ReturnType<typeof showUserLoader>>;

export const useShowUserLoaderData = (): ShowUserLoaderReturn => {
  return useLoaderData() as ShowUserLoaderReturn;
};

export const showUserLoader = async (id: string) => {
  return await GammaClient.instance().users.getUser(id);
};
