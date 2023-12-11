import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

type ShowSuperGroupLoaderReturn = Awaited<
  ReturnType<typeof showSuperGroupLoader>
>;

export const useShowSuperGroupLoaderData = (): ShowSuperGroupLoaderReturn => {
  return useLoaderData() as ShowSuperGroupLoaderReturn;
};

export const showSuperGroupLoader = async (id: string) => {
  return await GammaClient.instance().superGroups.getSuperGroup(id);
};
