import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma-client";

type GroupsLoaderReturn = Awaited<ReturnType<typeof groupsLoader>>;

export const useGroupsLoaderData = (): GroupsLoaderReturn => {
  return useLoaderData() as GroupsLoaderReturn;
};

export const groupsLoader = async () => {
  return await GammaClient.getInstance().groups.getGroups();
};
