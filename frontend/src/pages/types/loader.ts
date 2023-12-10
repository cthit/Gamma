import { useLoaderData } from "react-router-dom";
import { GammaClient } from "../../client/gamma-client";

type TypesLoaderReturn = Awaited<ReturnType<typeof typesLoader>>;

export const useTypesLoaderData = (): TypesLoaderReturn => {
  return useLoaderData() as TypesLoaderReturn;
};

export const typesLoader = async () => {
  return await GammaClient.getInstance().types.getTypes();
};
