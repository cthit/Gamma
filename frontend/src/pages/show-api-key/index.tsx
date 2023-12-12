import { useShowApiKeyLoaderData } from "./loader";

export const ShowApiKeyPage = () => {
  const apiKey = useShowApiKeyLoaderData();

  return (
    <div className={"mx-auto mt-4 w-fit"}>
      <ul className={"list-disc"}>
        <li>{apiKey.prettyName}</li>
        <li>{apiKey.svDescription}</li>
        <li>{apiKey.enDescription}</li>
        <li>{apiKey.keyType}</li>
      </ul>
    </div>
  );
};
