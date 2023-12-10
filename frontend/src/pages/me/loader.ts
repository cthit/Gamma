import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma-client";

type MeLoaderReturn = Awaited<ReturnType<typeof meLoader>>;

export const useMeLoaderData = (): MeLoaderReturn => {
  return useLoaderData() as MeLoaderReturn;
};

export const meLoader = async () => {
  return await GammaClient.getInstance().users.getMe();
};
