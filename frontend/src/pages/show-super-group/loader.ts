import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma-client";

type ShowSuperGroupLoaderReturn = Awaited<
  ReturnType<typeof showSuperGroupLoader>
>;

export const useShowSuperGroupLoaderData = (): ShowSuperGroupLoaderReturn => {
  return useLoaderData() as ShowSuperGroupLoaderReturn;
};

export const showSuperGroupLoader = async (id: string) => {
  return await GammaClient.getInstance().superGroups.getSuperGroup(id);
};
