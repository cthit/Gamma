import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

type GroupsLoaderReturn = Awaited<ReturnType<typeof groupsLoader>>;

export const useGroupsLoaderData = (): GroupsLoaderReturn => {
  return useLoaderData() as GroupsLoaderReturn;
};

export const groupsLoader = async () => {
  return {
    superGroups: await GammaClient.instance().superGroups.getSuperGroups(),
    groups: await GammaClient.instance().groups.getGroups(),
  };
};
