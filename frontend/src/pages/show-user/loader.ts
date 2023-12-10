import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma-client";

type ShowUserLoaderReturn = Awaited<ReturnType<typeof showUserLoader>>;

export const useShowUserLoaderData = (): ShowUserLoaderReturn => {
  return useLoaderData() as ShowUserLoaderReturn;
};

export const showUserLoader = async (id: string) => {
  return await GammaClient.getInstance().users.getUser(id);
};
