import { usePostsLoaderData } from "./loader";
import { Link } from "react-router-dom";

const tableCellStyle = "border border-slate-300 p-1";

export const PostsPage = () => {
  const posts = usePostsLoaderData();

  return (
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
          </tr>
        ))}
      </tbody>
    </table>
  );
};
