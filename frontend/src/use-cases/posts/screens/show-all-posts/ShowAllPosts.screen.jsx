import { DigitDesign, DigitFAB, DigitLayout, DigitTable, DigitTranslations } from "@cthit/react-digit-components";
import React from "react";
import translations from "./ShowAllPosts.screen.translations.json";
import AddPostButton from "./elements/add-post-button";

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
                <AddPostButton/>
            </DigitLayout.Fill>
        )}
    />
);

export default ShowAllPosts;
