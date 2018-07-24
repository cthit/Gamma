import React from "react";
import GammaTable from "../../../../common/views/gamma-table";

const ShowAllPosts = ({ posts, text }) => (
  <GammaTable
    idProp="id"
    startOrderBy="sv"
    columnsOrder={["id", "sv", "en"]}
    headerTexts={{
      id: "Id",
      sv: "Svenska",
      en: "Engelska",
      __link: "Detaljer"
    }}
    data={posts.map(post => {
      return {
        ...post,
        __link: "/posts/" + post.id
      };
    })}
  />
);

export default ShowAllPosts;
