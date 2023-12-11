import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

type ShowGroupLoaderReturn = Awaited<ReturnType<typeof showGroupLoader>>;

export const useShowGroupLoaderData = (): ShowGroupLoaderReturn => {
  return useLoaderData() as ShowGroupLoaderReturn;
};

export const showGroupLoader = async (id: string) => {
  return await GammaClient.instance().groups.getGroup(id);
};
