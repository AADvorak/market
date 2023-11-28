export function usePageAndFilter() {
  const
      filter = ref(''),
      currentPage = ref(1)

  function setFilter(value) {
    filter.value = value
    currentPage.value = 1
  }
  function setCurrentPage(value) {
    currentPage.value = value
  }
  function readUrlParams() {
    const route = useRoute()
    const pageParam = ref(route.query.page)
    if (pageParam.value) {
      currentPage.value = parseInt(pageParam.value)
    }
    const filterParam = ref(route.query.filter)
    if (filterParam.value) {
      filter.value = filterParam.value
    }
  }
  function setUrlParams() {
    const query = {page: currentPage.value}
    if (filter.value) {
      query.filter = filter.value
    }
    useRouter().replace({query})
  }

  return {filter, currentPage, setFilter, setCurrentPage, readUrlParams, setUrlParams}
}
