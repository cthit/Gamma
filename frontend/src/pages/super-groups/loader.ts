import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

type SuperGroupsLoaderReturn = Awaited<ReturnType<typeof superGroupsLoader>>;

export const useSuperGroupsLoaderData = (): SuperGroupsLoaderReturn => {
  return useLoaderData() as SuperGroupsLoaderReturn;
};

export const superGroupsLoader = async () => {
  return {
    types: await GammaClient.instance().types.getTypes(),
    superGroups: await GammaClient.instance().superGroups.getSuperGroups(),
  };
};
