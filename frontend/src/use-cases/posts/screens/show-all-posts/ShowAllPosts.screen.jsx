import React from "react";
import GammaTable from "../../../../common/views/gamma-table";
import { Fill } from "../../../../common-ui/layout";
import GammaFABButton from "../../../../common/elements/gamma-fab-button";
import { Add } from "@material-ui/icons";
import { GammaLink } from "../../../../common-ui/design";

const ShowAllPosts = ({ posts, text }) => (
  <Fill>
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
      emptyTableText="Det finns inga poster"
    />
    <GammaLink to="/posts/add">
      <GammaFABButton component={Add} secondary />
    </GammaLink>
  </Fill>
);

export default ShowAllPosts;
