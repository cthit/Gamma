import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

type SuperGroupsLoaderReturn = Awaited<ReturnType<typeof superGroupsLoader>>;

export const useSuperGroupsLoaderData = (): SuperGroupsLoaderReturn => {
  return useLoaderData() as SuperGroupsLoaderReturn;
};

export const superGroupsLoader = async () => {
  return await GammaClient.instance().superGroups.getSuperGroups();
};
