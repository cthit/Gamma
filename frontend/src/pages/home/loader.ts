import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma-client";

type HomeLoaderReturn = Awaited<ReturnType<typeof homeLoader>>;

export const useHomeLoaderData = (): HomeLoaderReturn => {
  return useLoaderData() as HomeLoaderReturn;
};

export const homeLoader = async () => {
  const user = await GammaClient.getInstance().users.getMe();
  return {
    user,
  };
};
