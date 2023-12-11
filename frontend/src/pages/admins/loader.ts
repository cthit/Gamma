import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

type AdminsLoaderReturn = Awaited<ReturnType<typeof adminsLoader>>;

export const useAdminsLoaderData = (): AdminsLoaderReturn => {
  return useLoaderData() as AdminsLoaderReturn;
};

export const adminsLoader = async () => {
  return {
    admins: await GammaClient.instance().admins.getAdmins(),
    users: await GammaClient.instance().users.getUsers(),
  };
};
