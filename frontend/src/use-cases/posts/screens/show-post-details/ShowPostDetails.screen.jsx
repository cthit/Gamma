import {
    DigitButton,
    DigitDisplayData,
    DigitText,
    DigitTranslations,
    DigitDesign
} from "@cthit/react-digit-components";
import React from "react";
import { Center, Fill, Spacing } from "../../../../common-ui/layout";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import translations from "./ShowPostDetails.screen.translations.json";

function getPostName(post, activeLanguage) {
    switch (activeLanguage.code.toLowerCase()) {
        case "en":
            return post.en;
        case "sv":
            return post.sv;
        default:
            post.en + "/" + post.sv;
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
            gammaDialogOpen,
            toastOpen,
            redirectTo,
            postsDelete
        } = this.props;

        return (
            <IfElseRendering
                test={post != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        uniquePath="Posts.Screen.ShowPostDetails"
                        render={(text, activeLanguage) => (
                            <Fill>
                                <Center>
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
                                            <Spacing />
                                            <DigitButton
                                                text={text.DeletePost}
                                                onClick={() => {
                                                    gammaDialogOpen({
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
                                </Center>
                            </Fill>
                        )}
                    />
                )}
            />
        );
    }
}

export default ShowPostDetails;
