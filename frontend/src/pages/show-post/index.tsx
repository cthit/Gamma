import { useShowPostLoaderData } from "./loader";

export const ShowPostPage = () => {
  const post = useShowPostLoaderData();

  return (
    <div className={"mx-auto mt-4 w-fit"}>
      <ul className={"list-disc"}>
        <li>{post.svName}</li>
        <li>{post.enName}</li>
        <li>{post.emailPrefix}</li>
      </ul>
    </div>
  );
};
