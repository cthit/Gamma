import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

type AllowListLoaderReturn = Awaited<ReturnType<typeof allowListLoader>>;

export const useAllowListLoaderData = (): AllowListLoaderReturn => {
  return useLoaderData() as AllowListLoaderReturn;
};

export const allowListLoader = async () => {
  return await GammaClient.instance().allowList.getAllowList();
};
