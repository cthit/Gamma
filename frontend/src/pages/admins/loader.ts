import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma-client";

type AdminsLoaderReturn = Awaited<ReturnType<typeof adminsLoader>>;

export const useAdminsLoaderData = (): AdminsLoaderReturn => {
  return useLoaderData() as AdminsLoaderReturn;
};

export const adminsLoader = async () => {
  return {
    admins: await GammaClient.getInstance().admins.getAdmins(),
    users: await GammaClient.getInstance().users.getUsers(),
  };
};
