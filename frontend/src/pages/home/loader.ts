import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma";

type HomeLoaderReturn = Awaited<ReturnType<typeof homeLoader>>;

export const useHomeLoaderData = (): HomeLoaderReturn => {
  return useLoaderData() as HomeLoaderReturn;
};

export const homeLoader = async () => {
  const user = await GammaClient.instance().users.getMe();
  return {
    user,
  };
};
