import { useTypesLoaderData } from "./loader";

export const TypesPage = () => {
  const types = useTypesLoaderData();

  return (
    <div className={"mx-auto mt-4 w-fit"}>
      <ul className={"list-disc"}>
        {types.map((type) => (
          <li key={type}>{type}</li>
        ))}
      </ul>
    </div>
  );
};
