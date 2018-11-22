import {
  DigitFAB,
  DigitTable,
  DigitTranslations,
  DigitDesign,
  DigitLayout
} from "@cthit/react-digit-components";
import { Add } from "@material-ui/icons";
import React from "react";
import translations from "./ShowAllPosts.screen.translations.json";

const ShowAllPosts = ({ posts }) => (
  <DigitTranslations
    translations={translations}
    uniquePath="Posts.Screen.ShowAllPosts"
    render={text => (
      <DigitLayout.Fill>
        <DigitTable
          titleText={text.Posts}
          searchText={text.SearchForPosts}
          idProp="id"
          startOrderBy="sv"
          columnsOrder={["id", "sv", "en"]}
          headerTexts={{
            id: text.Id,
            sv: text.Swedish,
            en: text.English,
            __link: text.Details
          }}
          data={posts.map(post => {
            return {
              ...post,
              __link: "/posts/" + post.id
            };
          })}
          emptyTableText={text.NoPosts}
        />
        <DigitLayout.DownRightPosition>
          <DigitDesign.Link to="/posts/add">
            <DigitFAB icon={Add} secondary />
          </DigitDesign.Link>
        </DigitLayout.DownRightPosition>
      </DigitLayout.Fill>
    )}
  />
);

export default ShowAllPosts;
