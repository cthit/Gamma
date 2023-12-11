import { usePostsLoaderData } from "./loader";
import { Link, useRevalidator } from "react-router-dom";
import { GammaClient } from "../../client/gamma";
import * as z from "zod";

const tableCellStyle = "border border-slate-300 p-1";

const createPostValidation = z
  .object({
    svName: z.string(),
    enName: z.string(),
    emailPrefix: z.string(),
  })
  .strict();

export const PostsPage = () => {
  const posts = usePostsLoaderData();
  const revalidator = useRevalidator();

  const onSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const form = Object.fromEntries(new FormData(e.currentTarget));

    const validatedForm = createPostValidation.parse(form);
    GammaClient.instance()
      .posts.createPost(validatedForm)
      .then(() => revalidator.revalidate());
  };

  const deletePost = (id: string) => () => {
    GammaClient.instance()
      .posts.deletePost(id)
      .then(() => {
        revalidator.revalidate();
      });
  };

  return (
    <div className={"mx-auto mt-4 w-fit flex flex-col gap-4 w-1/2"}>
      <table
        className={
          "m-auto w-full table-fixed border-collapse border border-slate-400"
        }
      >
        <thead>
          <tr>
            <th className={tableCellStyle + " text-left"}>Swedish name</th>
            <th className={tableCellStyle + " text-left"}>English name</th>
            <th className={tableCellStyle + " text-left"}>Email prefix</th>
            <th className={tableCellStyle + " text-left"}>Details</th>
            <th className={tableCellStyle + " text-left"}>Delete</th>
          </tr>
        </thead>
        <tbody>
          {posts.map((post) => (
            <tr key={post.id}>
              <td className={tableCellStyle}>{post.svName}</td>
              <td className={tableCellStyle}>{post.enName}</td>
              <td className={tableCellStyle}>{post.emailPrefix}</td>
              <td className={tableCellStyle}>
                <Link to={"/posts/" + post.id}>Details</Link>
              </td>
              <td>
                <button onClick={deletePost(post.id)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className={"outline flex flex-col gap-2"}>
        <form className={"contents"} onSubmit={onSubmit}>
          <input
            className={"outline"}
            type="text"
            name="svName"
            placeholder={"Swedish name"}
          />
          <input
            className={"outline"}
            type="text"
            name="enName"
            placeholder={"English name"}
          />
          <input
            className={"outline"}
            type="text"
            name="emailPrefix"
            placeholder={"Email prefix"}
          />
          <button type={"submit"}>Add post</button>
        </form>
      </div>
    </div>
  );
};
