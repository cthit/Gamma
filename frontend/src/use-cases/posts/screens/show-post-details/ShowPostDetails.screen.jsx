import {
    DigitButton,
    DigitDesign,
    DigitDisplayData,
    DigitIfElseRendering,
    DigitLayout,
    DigitText,
    DigitTranslations
} from "@cthit/react-digit-components";
import React from "react";
import translations from "./ShowPostDetails.screen.translations.json";

function getPostName(post, activeLanguage) {
    switch (activeLanguage.code.toLowerCase()) {
        case "sv":
            return post.sv;
        default:
            // case "en":
            return post.en;
    }
}

class ShowPostDetails extends React.Component {
    constructor(props) {
        super();

        props.postsLoadUsage(props.postId);
    }

    render() {
        const {
            post,
            dialogOpen,
            toastOpen,
            redirectTo,
            postsDelete
        } = this.props;

        return (
            <DigitIfElseRendering
                test={post != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        uniquePath="Posts.Screen.ShowPostDetails"
                        render={(text, activeLanguage) => (
                            <DigitLayout.Fill>
                                <DigitLayout.Center>
                                    <DigitDesign.Card
                                        minWidth="300px"
                                        maxWidth="600px"
                                    >
                                        <DigitDesign.CardBody>
                                            <DigitText.Heading3 text="Post" />
                                            <DigitDisplayData
                                                data={post}
                                                keysText={{
                                                    id: text.Id,
                                                    sv: text.Sv,
                                                    en: text.En
                                                }}
                                                keysOrder={["id", "sv", "en"]}
                                            />
                                            <DigitText.Heading3 text="Usages" />
                                        </DigitDesign.CardBody>
                                        <DigitDesign.CardButtons
                                            reverseDirection
                                        >
                                            <DigitDesign.Link
                                                to={
                                                    "/posts/" +
                                                    post.id +
                                                    "/edit"
                                                }
                                            >
                                                <DigitButton
                                                    text="Redigera"
                                                    primary
                                                    raised
                                                />
                                            </DigitDesign.Link>
                                            <DigitLayout.Spacing />
                                            <DigitButton
                                                text={text.DeletePost}
                                                onClick={() => {
                                                    dialogOpen({
                                                        title:
                                                            text.WouldYouLikeToDelete +
                                                            " " +
                                                            getPostName(
                                                                post,
                                                                activeLanguage
                                                            ),
                                                        confirmButtonText:
                                                            text.DeletePost,
                                                        cancelButtonText:
                                                            text.Cancel,
                                                        onConfirm: () => {
                                                            postsDelete(post.id)
                                                                .then(
                                                                    response => {
                                                                        toastOpen(
                                                                            {
                                                                                text:
                                                                                    text.YouHaveDeleted +
                                                                                    " " +
                                                                                    getPostName(
                                                                                        post,
                                                                                        activeLanguage
                                                                                    )
                                                                            }
                                                                        );
                                                                        redirectTo(
                                                                            "/posts"
                                                                        );
                                                                    }
                                                                )
                                                                .catch(
                                                                    error => {
                                                                        toastOpen(
                                                                            {
                                                                                text:
                                                                                    text.SomethingWentWrong
                                                                            }
                                                                        );
                                                                    }
                                                                );
                                                        }
                                                    });
                                                }}
                                            />
                                        </DigitDesign.CardButtons>
                                    </DigitDesign.Card>
                                </DigitLayout.Center>
                            </DigitLayout.Fill>
                        )}
                    />
                )}
            />
        );
    }
}

export default ShowPostDetails;
