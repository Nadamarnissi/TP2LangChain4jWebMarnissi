Question : Comment gérer et réinitialiser la mémoire d'une conversation avec LangChain4j ?

J'ai utilisé ChatGPT pour comprendre le fonctionnement de ChatMemory et de MessageWindowChatMemory dans une application Web Jakarta EE.

L'IA m'a notamment aidée à comprendre pourquoi vider l'historique affiché dans la page JSF ne suffit pas à effacer les messages conservés par LangChain4j.

Ce que j'ai appris : j'ai compris la différence entre l'historique affiché à l'utilisateur et la mémoire utilisée par le modèle. J'ai également appris à utiliser chatMemory.clear() pour réinitialiser le contexte d'une conversation.

J'ai vérifié ce fonctionnement en donnant mon prénom à Gemini, puis en démarrant une nouvelle conversation pour vérifier qu'il ne s'en souvenait plus.