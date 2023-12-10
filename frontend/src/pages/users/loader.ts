import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma-client";

type UsersLoaderReturn = Awaited<ReturnType<typeof usersLoader>>;

export const useUsersLoaderData = (): UsersLoaderReturn => {
  return useLoaderData() as UsersLoaderReturn;
};

export const usersLoader = async () => {
  const users = await GammaClient.getInstance().users.getUsers();
  return {
    users,
  };
};
