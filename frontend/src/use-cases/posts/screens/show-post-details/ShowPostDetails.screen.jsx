import {
    DigitButton,
    DigitTranslations,
    DigitDisplayData
} from "@cthit/react-digit-components";
import React from "react";
import {
    GammaCard,
    GammaCardBody,
    GammaCardButtons,
    GammaLink
} from "../../../../common-ui/design";
import { Center, Fill, Spacing } from "../../../../common-ui/layout";
import { Display } from "../../../../common-ui/text";
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
                                    <GammaCard
                                        minWidth="300px"
                                        maxWidth="600px"
                                    >
                                        <GammaCardBody>
                                            <Display text="Post" />
                                            <DigitDisplayData
                                                data={post}
                                                keysText={{
                                                    id: text.Id,
                                                    sv: text.Sv,
                                                    en: text.En
                                                }}
                                                keysOrder={["id", "sv", "en"]}
                                            />
                                            <Display text="Usages" />
                                        </GammaCardBody>
                                        <GammaCardButtons reverseDirection>
                                            <GammaLink
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
                                            </GammaLink>
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
                                        </GammaCardButtons>
                                    </GammaCard>
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
