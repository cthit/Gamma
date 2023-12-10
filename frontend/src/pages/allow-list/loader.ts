import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma-client";

type AllowListLoaderReturn = Awaited<ReturnType<typeof allowListLoader>>;

export const useAllowListLoaderData = (): AllowListLoaderReturn => {
  return useLoaderData() as AllowListLoaderReturn;
};

export const allowListLoader = async () => {
  return await GammaClient.getInstance().allowList.getAllowList();
};
